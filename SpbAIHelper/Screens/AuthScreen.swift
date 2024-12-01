//
//  AuthScreen.swift
//  SpbAIHelper
//
//  Created by MacBook on 30.11.2024.
//

import SwiftUI

struct AuthScreen: View {
    
    @State private var isInfoPresented = false
    @State private var isMainPresented = false
    
    @State private var isLoading = false

    private let userRepository = UserRepository()
    
    init(isInfoPresented: Bool = false) {
        self.isInfoPresented = isInfoPresented
        if(userRepository.checkAuth()){
            isMainPresented = true
        }
    }
    
    var body: some View {
        ZStack{
            Color.white
            VStack{
                Text("Войти с помощью")
                    .font(.title)
                Spacer()
                    .frame(height: 25)
                Button(action: {
                    
                    Task {
                        isLoading = true
                        try? await Task.sleep(nanoseconds: 3_000_000_000)
                        isLoading = false
                        isInfoPresented = true
                    }
                    
                }) {
                    ZStack{
                        Color.white
                        HStack{
                            Image("google")
                            Text("Google")
                                .font(.headline) // Шрифт текста
                                .foregroundColor(.black) // Цвет текста
                                .padding()
                        }
                    }
                }
                .frame(maxHeight: 55)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.gray.opacity(0.4), lineWidth: 2) // Обводка кнопки
                )
                .padding() // Внешние отступы кнопки
            }
        }
        .fullScreenCover(isPresented: $isInfoPresented){
            InfoScreen()
        }
    }

}

#Preview {
    AuthScreen()
}
