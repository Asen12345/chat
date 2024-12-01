import SwiftUI
import MapKit
import CoreLocation

// Структура для обертки CLLocationCoordinate2D
struct IdentifiableLocation: Identifiable {
    let id = UUID() // Уникальный идентификатор
    let coordinate: CLLocationCoordinate2D
}



struct InfoScreen: View {
    
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 37.7749, longitude: -122.4194), // Стартовая позиция
        span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05) // Масштаб карты
    )
    
    @State private var isMainScreenPresented = false
    
    @State private var selectedLocation: IdentifiableLocation? // Выбранная геолокация
    @State private var locationName: String = "Выберите место"
    
    var body: some View {
        VStack {
            
            Text("Выберете ваше местоположение")
                .font(.title2)
            Spacer()
                .frame(height: 20)
            ZStack{
                Map(
                    coordinateRegion: $region,
                    interactionModes: .all,
                    annotationItems: selectedLocation != nil ? [selectedLocation!] : [],
                    annotationContent: { location in
                        MapPin(coordinate: location.coordinate, tint: .blue)
                    }
                )
                .onTapGesture {
                    selectLocation(at: region.center)
                }
                .frame(height: 400)
            }
            .padding(.horizontal, 20)
            
            Text(locationName)
                .font(.headline)
                .padding()
            
            Spacer()
            
            Button(action: {
                isMainScreenPresented = true
            }) {
                Text("Подтвердить выбор")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
            .padding()
        }
        .fullScreenCover(isPresented: $isMainScreenPresented){
            MainScreen()
        }
    }
    
    // Метод для выбора геолокации
    private func selectLocation(at coordinate: CLLocationCoordinate2D) {
        selectedLocation = IdentifiableLocation(coordinate: coordinate)
        fetchLocationName(for: coordinate)
    }
    
    // Получение имени места по координатам
    private func fetchLocationName(for coordinate: CLLocationCoordinate2D) {
        let geocoder = CLGeocoder()
        let location = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
        
        geocoder.reverseGeocodeLocation(location) { placemarks, error in
            if let placemark = placemarks?.first {
                locationName = placemark.name ?? "Неизвестное место"
            } else {
                locationName = "Не удалось определить место"
            }
        }
    }
}

