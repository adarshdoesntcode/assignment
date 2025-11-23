import { useState } from "react";
import { Transaction } from "../../types/transaction";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";

interface TransactionListProps {
  transactions: Transaction[];
}

export const TransactionList = ({ transactions }: TransactionListProps) => {
  const [selectedTxn, setSelectedTxn] = useState<Transaction | null>(null);

  if (!transactions || transactions.length === 0) {
    return (
      <div className="p-12 text-center text-slate-500 text-base">
        No transactions found
      </div>
    );
  }

  const getStatusClass = (status: string) => {
    switch (status.toLowerCase()) {
      case "completed":
        return "bg-green-100 text-green-800";
      case "pending":
        return "bg-amber-100 text-amber-800";
      case "failed":
        return "bg-red-100 text-red-800";
      default:
        return "bg-slate-100 text-slate-800";
    }
  };

  const formatAmount = (amount: number, currency: string) => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: currency || "USD",
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <>
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border border-slate-200">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[800px] border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200">
                <th className="p-4 text-left font-semibold text-sm text-slate-600 uppercase tracking-wider">
                  Transaction ID
                </th>
                <th className="p-4 text-left font-semibold text-sm text-slate-600 uppercase tracking-wider">
                  Date
                </th>
                <th className="p-4 text-left font-semibold text-sm text-slate-600 uppercase tracking-wider">
                  Amount
                </th>
                <th className="p-4 text-left font-semibold text-sm text-slate-600 uppercase tracking-wider">
                  Card
                </th>
                <th className="p-4 text-left font-semibold text-sm text-slate-600 uppercase tracking-wider">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {transactions.map((txn) => (
                <tr
                  key={txn.txnId}
                  className="hover:bg-slate-50 transition-colors cursor-pointer"
                  onClick={() => setSelectedTxn(txn)}
                >
                  <td className="p-4 text-sm font-mono font-semibold text-blue-600">
                    #{txn.txnId}
                  </td>
                  <td className="p-4 text-sm text-slate-800">
                    {formatDate(txn.timestamp)}
                  </td>
                  <td className="p-4 text-sm font-semibold text-emerald-600">
                    {formatAmount(txn.amount, txn.currency)}
                  </td>
                  <td className="p-4 text-sm">
                    <span className="font-mono text-slate-500">
                      {txn.cardType} •••• {txn.cardLast4}
                    </span>
                  </td>
                  <td className="p-4 text-sm">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold uppercase ${getStatusClass(
                        txn.status
                      )}`}
                    >
                      {txn.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <Dialog open={!!selectedTxn} onOpenChange={(open) => !open && setSelectedTxn(null)}>
        <DialogContent className="sm:max-w-[600px]">
          <DialogHeader>
            <DialogTitle className="text-xl font-bold flex items-center gap-3">
              Transaction Details
              {selectedTxn && (
                <span
                  className={`px-3 py-1 rounded-full text-xs font-semibold uppercase ${getStatusClass(
                    selectedTxn.status
                  )}`}
                >
                  {selectedTxn.status}
                </span>
              )}
            </DialogTitle>
            <DialogDescription>
              Transaction ID: <span className="font-mono font-semibold text-slate-700">#{selectedTxn?.txnId}</span>
            </DialogDescription>
          </DialogHeader>

          {selectedTxn && (
            <div className="space-y-6 py-4">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-slate-500 mb-1">Date & Time</p>
                  <p className="font-medium">{formatDate(selectedTxn.timestamp)}</p>
                </div>
                <div>
                  <p className="text-slate-500 mb-1">Card Information</p>
                  <p className="font-medium font-mono">
                    {selectedTxn.cardType} •••• {selectedTxn.cardLast4}
                  </p>
                </div>
                <div>
                  <p className="text-slate-500 mb-1">Acquirer</p>
                  <p className="font-medium">{selectedTxn.acquirer}</p>
                </div>
                <div>
                  <p className="text-slate-500 mb-1">Issuer</p>
                  <p className="font-medium">{selectedTxn.issuer}</p>
                </div>
              </div>

              <div className="border-t border-slate-100 pt-4">
                <h4 className="font-semibold mb-3 text-sm uppercase text-slate-500 tracking-wider">Payment Breakdown</h4>
                <div className="space-y-3">
                  {selectedTxn.details.map((detail) => (
                    <div key={detail.detailId} className="flex justify-between items-center text-sm">
                      <div>
                        <span className="font-medium text-slate-700 capitalize">{detail.type}</span>
                        <span className="text-slate-400 mx-2">-</span>
                        <span className="text-slate-500">{detail.description}</span>
                      </div>
                      <span className="font-mono font-medium">
                        {formatAmount(detail.amount, selectedTxn.currency)}
                      </span>
                    </div>
                  ))}
                  <div className="flex justify-between items-center text-base font-bold pt-3 border-t border-slate-100 mt-3">
                    <span>Total Amount</span>
                    <span className="text-emerald-600">
                      {formatAmount(selectedTxn.amount, selectedTxn.currency)}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
};
