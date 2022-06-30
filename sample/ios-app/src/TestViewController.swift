//
//  Created by Aleksey Mikhailov on 23/06/2019.
//  Copyright © 2019 IceRock Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary

class TestViewController: UIViewController {
    
    @IBOutlet private var label: UILabel!
    
    private var viewModel: SampleViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        label.text = "wait press..."
        
        viewModel = SampleViewModel(eventsDispatcher: EventsDispatcher(listener: self),
                                    permissionsController: PermissionsController(),
                                    permissionType: Permission.recordAudio)
    }
    
    @IBAction func onPermissionPressed() {
        viewModel.onRequestPermissionButtonPressed()
    }
}

extension TestViewController: SampleViewModelEventListener {
    func onSuccess() {
        label.text = "success granted"
    }
    
    func onDenied(exception: DeniedException) {
        label.text = "denied" // on ios is impossible
    }
    
    func onDeniedAlways(exception: DeniedAlwaysException) {
        label.text = "denied always"
    }
}
