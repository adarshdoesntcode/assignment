import { useState } from "react";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Transaction } from "@/types/transaction";

interface TransactionListProps {
  transactions: Transaction[];
}

export const TransactionList = ({ transactions }: TransactionListProps) => {
  const [selectedTxn, setSelectedTxn] = useState<Transaction | null>(null);

  if (!transactions || transactions.length === 0) {
    return (
      <div className="p-12 text-base text-center text-slate-500">
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
    return new Date(dateString).toLocaleString("en-IN", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };
  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleString("en-IN", {
      // month: "short",
      // day: "numeric",
      // year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <>
      <div className="overflow-hidden bg-white border rounded-lg shadow-sm border-slate-200">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[800px] border-collapse">
            <thead>
              <tr className="border-b bg-slate-50 border-slate-200">
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Transaction ID
                </th>
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Date
                </th>
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Time
                </th>
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Amount
                </th>
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Card
                </th>
                <th className="p-4 text-sm font-semibold tracking-wider text-left uppercase text-slate-600">
                  Status
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {transactions.map((txn) => (
                <tr
                  key={txn.txnId}
                  className="transition-colors cursor-pointer hover:bg-slate-50"
                  onClick={() => setSelectedTxn(txn)}
                >
                  <td className="p-4 font-mono text-sm font-semibold text-blue-600">
                    #{txn.txnId}
                  </td>
                  <td className="p-4 text-sm text-slate-800">
                    {formatDate(txn.txnDate)}
                  </td>
                  <td className="p-4 text-sm text-slate-800">
                    {formatTime(txn.timestamp)}
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

      <Dialog
        open={!!selectedTxn}
        onOpenChange={(open) => !open && setSelectedTxn(null)}
      >
        <DialogContent className="sm:max-w-[600px]">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-3 text-xl font-bold">
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
              Transaction ID:{" "}
              <span className="font-mono font-semibold text-slate-700">
                #{selectedTxn?.txnId}
              </span>
            </DialogDescription>
          </DialogHeader>

          {selectedTxn && (
            <div className="py-4 space-y-6">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="mb-1 text-slate-500">Date & Time</p>
                  <p className="font-medium">
                    {formatDate(selectedTxn.timestamp)}
                  </p>
                </div>
                <div>
                  <p className="mb-1 text-slate-500">Card Information</p>
                  <p className="font-mono font-medium">
                    {selectedTxn.cardType} •••• {selectedTxn.cardLast4}
                  </p>
                </div>
                <div>
                  <p className="mb-1 text-slate-500">Acquirer</p>
                  <p className="font-medium">{selectedTxn.acquirer}</p>
                </div>
                <div>
                  <p className="mb-1 text-slate-500">Issuer</p>
                  <p className="font-medium">{selectedTxn.issuer}</p>
                </div>
              </div>

              <div className="pt-4 border-t border-slate-100">
                <h4 className="mb-3 text-sm font-semibold tracking-wider uppercase text-slate-500">
                  Payment Breakdown
                </h4>
                <div className="space-y-3">
                  {selectedTxn.details.map((detail) => (
                    <div
                      key={detail.detailId}
                      className="flex items-center justify-between text-sm"
                    >
                      <div>
                        <span className="font-medium capitalize text-slate-700">
                          {detail.type}
                        </span>
                        <span className="mx-2 text-slate-400">-</span>
                        <span className="text-slate-500">
                          {detail.description}
                        </span>
                      </div>
                      <span className="font-mono font-medium">
                        {formatAmount(detail.amount, selectedTxn.currency)}
                      </span>
                    </div>
                  ))}
                  <div className="flex items-center justify-between pt-3 mt-3 text-base font-bold border-t border-slate-100">
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
