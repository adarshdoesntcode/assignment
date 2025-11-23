import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App.tsx";
import "./App.css";

import { Toaster } from "./components/ui/sonner.tsx";

ReactDOM.createRoot(document.getElementById("root")!).render(
  // <React.StrictMode>
  <BrowserRouter>
    <Toaster />
    <App />
  </BrowserRouter>
  // </React.StrictMode>,
);
