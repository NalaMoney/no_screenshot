import Flutter
import UIKit

extension UIWindow {
 
    func makeSecure() -> UITextField {

        let field = UITextField()
        let view = UIView(frame: CGRect(x: 0, y: 0, width: field.frame.self.width, height: field.frame.self.height))
        self.addSubview(field)
        self.layer.superlayer?.addSublayer(field.layer)
        field.layer.sublayers?.last!.addSublayer(self.layer)
        field.leftView = view
        field.leftViewMode = .always
    
        return field
    }
}

public class SwiftNoScreenshotPlugin: NSObject, FlutterPlugin {
    private static var channel: FlutterMethodChannel? = nil
    static private var preventScreenShot: Bool = false
    static private var secureTextField: UITextField? = nil

    public static func register(with registrar: FlutterPluginRegistrar) {
        SwiftNoScreenshotPlugin.channel = FlutterMethodChannel(name: "com.flutterplaza.no_screenshot", binaryMessenger: registrar.messenger())

        if let window = UIApplication.shared.delegate?.window {
            secureTextField = window?.makeSecure()
        }

        let instance = SwiftNoScreenshotPlugin()
        registrar.addMethodCallDelegate(instance, channel: SwiftNoScreenshotPlugin.channel!)
        registrar.addApplicationDelegate(instance)
    }

    public func applicationWillResignActive(_ application: UIApplication) {   
        applyScreenshotPolicy()
    }

    public func applicationDidBecomeActive(_ application: UIApplication) {
                    
        applyScreenshotPolicy()
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
     
        if (call.method == "screenshotOff") {
            SwiftNoScreenshotPlugin.preventScreenShot = true
            applyScreenshotPolicy()

        } else if (call.method == "screenshotOn") {
            SwiftNoScreenshotPlugin.preventScreenShot = false
            applyScreenshotPolicy()
        } else if (call.method == "toggleScreenshot") {
       
            SwiftNoScreenshotPlugin.preventScreenShot = !SwiftNoScreenshotPlugin.preventScreenShot;
            applyScreenshotPolicy()
        }
        result(true)
    }

    private func applyScreenshotPolicy() {
        SwiftNoScreenshotPlugin.secureTextField?.isSecureTextEntry = SwiftNoScreenshotPlugin.preventScreenShot
    }
}
