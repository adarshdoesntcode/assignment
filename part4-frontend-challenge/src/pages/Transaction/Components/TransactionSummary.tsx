interface TransactionSummaryProps {
  summary: {
    totalTransactions?: number;
    totalAmount?: number;
    currency?: string;
    byStatus?: Record<string, number>;
  };
}

export const TransactionSummary = ({ summary }: TransactionSummaryProps) => {
  const totalAmount = summary.totalAmount;
  const currency = summary.currency || "USD";

  const formatAmount = (amount: number) => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: currency,
    }).format(amount);
  };

  return (
    <div className="grid grid-cols-1 gap-6 my-4 sm:grid-cols-2 lg:grid-cols-5">
      <div className="bg-white p-6 rounded-lg shadow-sm hover:-translate-y-0.5 hover:shadow-md transition-all duration-200 border border-slate-100">
        <div className="mb-2 text-sm font-medium tracking-wider uppercase text-slate-500">
          Total Transactions
        </div>
        <div className="text-2xl font-bold text-slate-900">
          {summary?.totalTransactions || 0}
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-sm hover:-translate-y-0.5 hover:shadow-md transition-all duration-200 border border-slate-100">
        <div className="mb-2 text-sm font-medium tracking-wider uppercase text-slate-500">
          Total Amount
        </div>
        <div className="text-2xl font-bold truncate text-emerald-600">
          {formatAmount(totalAmount || 0)}
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-sm hover:-translate-y-0.5 hover:shadow-md transition-all duration-200 border border-slate-100">
        <div className="mb-2 text-sm font-medium tracking-wider uppercase text-slate-500">
          Completed
        </div>
        <div className="text-2xl font-bold text-emerald-600">
          {summary?.byStatus?.completed || 0}
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-sm hover:-translate-y-0.5 hover:shadow-md transition-all duration-200 border border-slate-100">
        <div className="mb-2 text-sm font-medium tracking-wider uppercase text-slate-500">
          Pending
        </div>
        <div className="text-2xl font-bold text-amber-600">
          {summary?.byStatus?.pending || 0}
        </div>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-sm hover:-translate-y-0.5 hover:shadow-md transition-all duration-200 border border-slate-100">
        <div className="mb-2 text-sm font-medium tracking-wider uppercase text-slate-500">
          Failed
        </div>
        <div className="text-2xl font-bold text-red-600">
          {summary?.byStatus?.failed || 0}
        </div>
      </div>
    </div>
  );
};
