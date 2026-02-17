import ComposeApp
import SwiftUI
import UIKit

// 各タブのComposeコンテンツをラップするビュー
struct ComposeTabView: UIViewControllerRepresentable {
    let tabPosition: Int32

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.TabContentViewController(tabPosition: tabPosition)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State private var selectedTab: Int = 0

    private let tabs = MainViewControllerKt.getTabItems()

    var body: some View {
        TabView(selection: $selectedTab) {
            ForEach(Array(tabs.enumerated()), id: \.element.position) { _, tab in
                ComposeTabView(tabPosition: tab.position)
                    .ignoresSafeArea()
                    .tabItem {
                        Image(systemName: tab.iconName)
                        Text(tab.title)
                    }
                    .tag(Int(tab.position))
            }
        }
    }
}
