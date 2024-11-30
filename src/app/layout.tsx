import "@/styles/global.scss"
import localFont from 'next/font/local'

export const metadata = {
  title: 'Я здесь живу',
  description: 'Чат бот "Я здесь живу"',
}

const golosFont = localFont({
  src: [
    { path: "../fonts/GolosText-Regular.ttf", weight: "400" },
    { path: "../fonts/GolosText-Medium.ttf", weight: "500" },
    { path: "../fonts/GolosText-Bold.ttf", weight: "700" },
  ]
});

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className={golosFont.className}>
      <body>{children}</body>
    </html>
  )
}
