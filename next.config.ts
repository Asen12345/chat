import type { NextConfig } from "next";

const path = require('path');

/** @type {import('next').NextConfig} */
const nextConfig = {
  sassOptions: {
    includePaths: [path.join(__dirname, 'src/styles')],
    additionalData: `@import "variables.scss";`,
  },
  // Другие настройки Next.js...
};

export default nextConfig;
