//
//  MainScreenViewModel.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

class Chat:Identifiable{
    let name:String
    let id:String
    init(name: String, id: String) {
        self.name = name
        self.id = id
    }
}

class MainViewModel: ObservableObject {
    
    @Published var chats: [Chat] = []
    @Published var userType:UserType = UserType.Resident
    
    
    func addChat(name:String){
        let id = UUID().uuidString
        chats.append(Chat(name: name, id: id))
    }
    
    func switchType(type:UserType){
        userType = type
    }
    
}
