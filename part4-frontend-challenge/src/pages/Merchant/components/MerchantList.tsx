import { Merchant } from "@/types/merchant";
import { ArrowUpDown, ArrowUp, ArrowDown } from "lucide-react";
import { useNavigate } from "react-router-dom";

interface MerchantListProps {
  merchants: Merchant[];
  sortBy?: string;
  sortDirection?: string;
  onSort: (field: string) => void;
}

function MerchantList({
  merchants,
  sortBy,
  sortDirection,
  onSort,
}: MerchantListProps) {
  const navigate = useNavigate();

  if (!merchants || merchants.length === 0) {
    return (
      <div className="p-12 text-base text-center text-slate-500">
        No merchants found
      </div>
    );
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  const getSortIcon = (field: string) => {
    const sortFields = sortBy?.split(",") || [];
    const sortDirections = sortDirection?.split(",") || [];
    const fieldIndex = sortFields.indexOf(field);

    if (fieldIndex === -1) {
      return <ArrowUpDown className="w-4 h-4 text-slate-400" />;
    }

    const direction = sortDirections[fieldIndex];
    return direction === "ASC" ? (
      <ArrowUp className="w-4 h-4 text-blue-600" />
    ) : (
      <ArrowDown className="w-4 h-4 text-blue-600" />
    );
  };

  const getBusinessTypeBadge = (type: string) => {
    const colorMap: Record<string, string> = {
      retail: "bg-blue-100 text-blue-800",
      restaurant: "bg-orange-100 text-orange-800",
      ecommerce: "bg-purple-100 text-purple-800",
      services: "bg-green-100 text-green-800",
      hospitality: "bg-pink-100 text-pink-800",
      healthcare: "bg-red-100 text-red-800",
    };

    return (
      <span
        className={`px-2 py-1 rounded-full text-xs font-semibold uppercase ${
          colorMap[type.toLowerCase()] || "bg-slate-100 text-slate-800"
        }`}
      >
        {type}
      </span>
    );
  };

  return (
    <div className="overflow-hidden bg-white border rounded-lg shadow-sm border-slate-200">
      <div className="overflow-x-auto">
        <table className="w-full min-w-[1000px] border-collapse">
          <thead>
            <tr className="border-b bg-slate-50 border-slate-200">
              <th
                className="p-4 text-sm font-semibold tracking-wider text-left uppercase transition-colors cursor-pointer text-slate-600 hover:bg-slate-100"
                onClick={() => onSort("merchantId")}
              >
                <div className="flex items-center gap-2">
                  Merchant ID
                  {getSortIcon("merchantId")}
                </div>
              </th>
              <th
                className="p-4 text-sm font-semibold tracking-wider text-left uppercase transition-colors cursor-pointer text-slate-600 hover:bg-slate-100"
                onClick={() => onSort("merchantName")}
              >
                <div className="flex items-center gap-2">
                  Merchant Name
                  {getSortIcon("merchantName")}
                </div>
              </th>
              <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                Business Name
              </th>
              <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                Contact
              </th>
              <th
                className="p-4 text-sm font-semibold tracking-wider text-left uppercase transition-colors cursor-pointer text-slate-600 hover:bg-slate-100"
                onClick={() => onSort("businessType")}
              >
                <div className="flex items-center gap-2">
                  Type
                  {getSortIcon("businessType")}
                </div>
              </th>
              <th
                className="p-4 text-sm font-semibold tracking-wider text-left uppercase transition-colors cursor-pointer text-slate-600 hover:bg-slate-100"
                onClick={() => onSort("createdAt")}
              >
                <div className="flex items-center gap-2">
                  Created
                  {getSortIcon("createdAt")}
                </div>
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {merchants.map((merchant) => (
              <tr
                key={merchant.merchantId}
                className="transition-colors cursor-pointer hover:bg-slate-50"
                onClick={() => {
                  navigate(`/merchants/${merchant.merchantId}`);
                }}
              >
                <td className="p-4 font-mono text-sm font-semibold text-blue-600">
                  {merchant.merchantId}
                </td>
                <td className="p-4 text-sm font-semibold text-slate-900">
                  {merchant.merchantName}
                </td>
                <td className="p-4 text-sm text-slate-700">
                  {merchant.businessName}
                </td>
                <td className="p-4 py-2 text-sm">
                  <div className="flex flex-col gap-1">
                    <span className="text-slate-700">{merchant.email}</span>
                    <span className="font-mono text-xs text-slate-500">
                      {merchant.phone}
                    </span>
                  </div>
                </td>
                <td className="p-4 text-sm">
                  {getBusinessTypeBadge(merchant.businessType)}
                </td>
                <td className="p-4 text-sm text-slate-600">
                  {formatDate(merchant.createdAt)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default MerchantList;
