//
//  RoundedButton.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

struct RoundedButton: View {
    
    let title:String
    let callback:() -> Void
    let checkIsActive: Bool
    
    init(title: String, callback: @escaping () -> Void, checkIsActive:Bool = false) {
        self.title = title
        self.callback = callback
        self.checkIsActive = checkIsActive
    }
    
    var body: some View {
        Button(action: {
            callback()
        }) {
            HStack{
                if(checkIsActive){
                    Image(systemName: "checkmark.circle")
                        .tint(.black)
                }
                Text(title)
                    .font(.caption)
                    .foregroundColor(.black)
            }
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color.white)
            .cornerRadius(10)
            .overlay(
               RoundedRectangle(cornerRadius: 12)
                 .stroke(Color.gray.opacity(0.2), lineWidth: 1)
           )
        }
    }
}


