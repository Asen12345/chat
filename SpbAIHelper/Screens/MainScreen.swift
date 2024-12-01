//
//  MainScreen.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

struct MainScreen: View {
    
    @State private var isChatPresenred = false
    
    @State private var showDialog = false
    @State private var userInput = ""
    
    @StateObject private var viewModel = MainViewModel()
    
    var body: some View {
        ZStack(alignment:.top){
            Color("background")
                .opacity(0.6)
                .ignoresSafeArea()
            VStack{
                VStack{
                    Spacer()
                        .frame(height: 20)
                    HStack{
                        VStack(alignment: .leading){
                            Text("Умный AI помощник СПБ")
                                .font(.title3)
                                .fontWeight(.semibold)
                            Spacer()
                                .frame(height: 5)
                            Text("Задайте любой интересующий вопрос")
                                .font(.caption)
                        }
                        Spacer()
                        Image("spb_logo")
                            .frame(width: 50, height: 50)
                            .scaledToFit()
                            .clipShape(/*@START_MENU_TOKEN@*/Circle()/*@END_MENU_TOKEN@*/)
                    }
                    Spacer()
                        .frame(height: 15)
                    HStack{
                        RoundedButton(
                            title: "Турист",
                            callback: {
                                viewModel.switchType(type: UserType.Tourist)
                            },
                            checkIsActive: viewModel.userType == UserType.Tourist
                        )
                        Spacer()
                            .frame(width: 20)
                        RoundedButton(
                            title: "Житель", callback: {
                                viewModel.switchType(type: UserType.Resident)
                            },
                            checkIsActive: viewModel.userType == UserType.Resident
                        )
                    }
                    Spacer()
                        .frame(height: 25)
                    
                    List(viewModel.chats) { chat in
                        VStack(alignment:.leading){
                            Text(chat.name)
                                .fontWeight(.semibold)
                            Spacer()
                                .frame(height: 10)
                            Text("0 сообщений")
                                .font(.caption)
                            
                        }
                   }
                    .listStyle(PlainListStyle())
                    
                    Spacer()
                        .frame(height: 20)
                    
                    Button(action: {
                        showDialog = true
                    }) {
                        HStack{
                            Image(systemName: "plus.circle")
                                .tint(.black)
                            Text("Новый чат")
                                .font(.caption)
                                .foregroundColor(.black)
                                .padding()
                        }
                        .frame(maxWidth: .infinity)
                        .background(Color.white)
                        .cornerRadius(10)
                        .overlay(
                           RoundedRectangle(cornerRadius: 12)
                             .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                       )
                    }
                    
                    VStack{
                        Spacer()
                            .frame(height: 60)
                    }
                }
                .padding(.horizontal, 20)
                Spacer()
            }
            .ignoresSafeArea(edges: .bottom)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .fullScreenCover(isPresented:$isChatPresenred){
                ChatScreen()
            }
            .alert("Введите название чата", isPresented: $showDialog) {
               TextField("Название..", text: $userInput)
               Button("Продолжить") {
                   viewModel.addChat(name: userInput)
                   userInput = ""
               }
               Button("Отмена", role: .cancel) {
                   userInput = ""
               }
           }
        }
    }
}

#Preview {
    MainScreen()
}
