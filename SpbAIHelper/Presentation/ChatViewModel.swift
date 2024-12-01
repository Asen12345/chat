//
//  ChatViewModel.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

struct Message:Identifiable, Equatable {
    var id = UUID()
    let message:String
    var isUserMessage:Bool
    
    init(message: String, isUserMessage: Bool) {
        self.message = message
        self.isUserMessage = isUserMessage
    }
}

class ChatViewModel: ObservableObject {
    @Published var messages: [Message] = [Message(message: "ыыыы даун", isUserMessage: false)]

    func addMessage(message: String) {
        let message = Message(message: message, isUserMessage:true)
        messages.append(message)
    }
}
