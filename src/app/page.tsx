"use client";
import { useState } from "react";
import GetLocation from "@/components/GetLocation"
import Chat from "@/components/Chat"

export default function Home() {
  const [openChat, setopenChat] = useState(false)
  return (
    openChat ? <Chat goBack={() => setopenChat(false)} /> : <GetLocation goToChat={() => setopenChat(true)} />
  );
}
