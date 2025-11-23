import { Routes, Route } from "react-router-dom";
import { Header } from "./components/layout/Header";

import { Merchants } from "./pages/Merchant/Merchants";
import { Reports } from "./pages/Reports";
import "./App.css";
import { Transactions } from "./pages/Transaction/Transactions";

function App() {
  return (
    <div className="app">
      <Header />

      <Routes>
        <Route path="/" element={<Transactions />} />
        <Route path="/merchants" element={<Merchants />} />
        <Route path="/merchants/:id" element={<Reports />} />
        <Route path="/reports" element={<Reports />} />
      </Routes>
    </div>
  );
}

export default App;
