//
//  UserRepository.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

class UserRepository{
    
    private let defaults = UserDefaults.standard

    
    func fetchDeviceId(){
        var id  = defaults.string(forKey: "user_id") ?? nil
        if(id == nil){
            id = UUID().uuidString
            defaults.setValue(id, forKey: "user_id")
        }
    }
    
    func addChat(){
        
        
    }
    
    func checkAuth() -> Bool{
        var id  = defaults.string(forKey: "user_id")
        return id != nil
    }
    
}
