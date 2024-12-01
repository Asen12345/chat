//
//  ChatScreen.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI



struct ChatScreen: View {
    
    
    @StateObject private var viewModel = ChatViewModel()
    
    @State private var text: String = ""

    @State private var isMainPresented = false
    
    var body: some View {
        VStack{
            HStack(alignment:.center){
                Button(action: {
                    isMainPresented = true
                }) {
                    Image(systemName: "arrow.uturn.backward")
                        .font(.caption)
                        .foregroundColor(.black)
                        .padding()
                        .background(Color.white)
                        .cornerRadius(10)
                        .overlay(
                           RoundedRectangle(cornerRadius: 12)
                             .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                       )
                }
                Spacer()
                VStack{
                    Text("Новый чат")
                        .fontWeight(.semibold)
                    Text("\(viewModel.messages.count) сообщений")
                        .font(.caption)
                }
                Spacer()
                Button(action: {
                }) {
                    Image(systemName: "arrowshape.turn.up.right")
                        .font(.caption)
                        .foregroundColor(.black)
                        .padding()
                        .background(Color.white)
                        .cornerRadius(10)
                        .overlay(
                           RoundedRectangle(cornerRadius: 12)
                             .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                       )
                }
            }
            .padding(.bottom, 15)
            .padding(.horizontal, 20)
            .overlay(
                Rectangle()
                         .frame(height: 1)
                         .foregroundColor(Color.gray.opacity(0.2)), alignment: .bottom
           )
            ScrollViewReader { scrollProxy in
                ScrollView {
                   VStack(alignment: .leading, spacing: 10) {
                       ForEach(viewModel.messages) { message in
                           HStack {
                               if message.isUserMessage {
                                 Spacer()
                             }
                               ZStack {
                                   Text(message.message)
                                       .padding(10)
                                       .foregroundColor(.black) // Цвет текста
                               }
                               .overlay(
                                   RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.gray.opacity(message.isUserMessage ? 0.2 : 0.0), lineWidth: 1) // Граница
                               )
                               .background(
                                   RoundedRectangle(cornerRadius: 12)
                                    .fill(message.isUserMessage ? Color.white : Color("background")) 
                               )

                               
                               if !message.isUserMessage {
                                 Spacer()
                             }
                         }
                       }
                   }
                   .padding()
                   .onChange(of: viewModel.messages) { _ in
                       if let lastMessage = viewModel.messages.last {
                          scrollProxy.scrollTo(lastMessage.id, anchor: .bottom)
                      }
                  }
               }
            }
            HStack(alignment:.center){
                TextField("Введите сообщение...", text: $text)
                    .padding(.vertical, 10) // Padding for the text inside
                    .padding(.horizontal, 15) // Padding for the text inside
                    .overlay(
                        Rectangle()
                            .frame(height: 1) // Height of the bottom border
                            .foregroundColor(.gray.opacity(0.2)), alignment: .bottom
                    )
                    .background(Color.white)
                    .cornerRadius(5)
                Spacer()
                    .frame(width: 20)
                Button(action: {
                    viewModel.addMessage(message: text)
                    text = ""
                }) {
                    HStack{
                        Image(systemName: "paperplane.fill")
                            .font(.caption)
                            .foregroundColor(.black)
                    }
                    .padding()
                    .background(Color.white)
                    .cornerRadius(5)
                    .overlay(
                       RoundedRectangle(cornerRadius: 12)
                         .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                   )
                }
            }
            .padding(.bottom, 7)
            .padding(.top, 16)
            .padding(.horizontal, 15)
            .overlay(
                Rectangle()
                         .frame(height: 1)
                         .foregroundColor(Color.gray.opacity(0.2)), alignment: .top
           )
        }
        .fullScreenCover(isPresented: $isMainPresented){
            MainScreen()
        }
    }
}

#Preview {
    ChatScreen()
}



