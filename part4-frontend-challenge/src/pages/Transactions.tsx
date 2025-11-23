import { useEffect, useState } from "react";
import { FilterState, DEFAULT_FILTERS } from "../types/transaction";
import { TransactionList } from "../components/common/TransactionList";
import { TransactionSummary } from "../components/common/TransactionSummary";
import { useTransactions } from "../hooks/useTransactions";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import TablePagination from "@/components/common/TablePagination";
import TransactionTableFilters from "@/components/common/TransactionTableFilters";
import { CloudCog } from "lucide-react";
import { Spinner } from "@/components/ui/spinner";

/**
 * Transactions Page Component
 * Displays transaction dashboard with summary and list
 */
export const Transactions = () => {
  const [id, setId] = useState("MCH-00001");
  const [searchValue, setSearchValue] = useState("");
  const [filters, setFilters] = useState<FilterState>(DEFAULT_FILTERS);
  const [size, setSize] = useState(10);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [status, setStatus] = useState("");

  console.log(filters);

  const { data, loading, error } = useTransactions(id, filters);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const search = searchValue.trim();
    if (!search) {
      return toast.warning("Please enter a search value");
    }
    setId(search.toUpperCase());
  };

  const handlePageChange = (page: number) => {
    setPage(page);
    setFilters({ ...filters, page });
  };

  const handlePageSizeChange = (size: number) => {
    setSize(size);
    setFilters({ ...filters, size });
  };

  const handleStatusChange = (status: string) => {
    setStatus(status);
    setFilters({ ...filters, status });
  };

  const handleDateRangeChange = (startDate: string, endDate: string) => {
    setStartDate(startDate);
    setEndDate(endDate);
    setFilters({ ...filters, startDate, endDate });
  };

  const handleReset = () => {
    setFilters(DEFAULT_FILTERS);
    setStatus("");
    setStartDate("");
    setEndDate("");
  };

  useEffect(() => {
    if (data && !error) {
      setTotalPages(data.pagination.totalPages);
      setPage(data.pagination.page);
      setSize(data.pagination.size);
      setTotalElements(data.pagination.totalElements);
    }
  }, [data]);

  return (
    <main className="container mx-auto px-4 py-8 space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold">Transaction Dashboard</h1>
          <p className="text-muted-foreground">Merchant: {data?.merchantId}</p>
        </div>
        <form onSubmit={handleSubmit} className="w-full md:w-auto">
          <Input
            className="w-full md:w-[300px]"
            value={searchValue}
            placeholder="Search Merchant Id"
            autoComplete="off"
            onChange={(e) => setSearchValue(e.target.value)}
          />
        </form>
      </div>

      {loading && (
        <div className="w-full flex items-center justify-center h-[500px]">
          <Spinner />
        </div>
      )}

      {data && (
        <>
          <TransactionSummary summary={data?.summary} />
          <TransactionTableFilters
            handleReset={handleReset}
            handleStatusChange={handleStatusChange}
            handleDateRangeChange={handleDateRangeChange}
            startDate={startDate}
            endDate={endDate}
            status={status}
          />
          <TransactionList transactions={data.transactions || []} />

          <TablePagination
            handlePageChange={handlePageChange}
            handlePageSizeChange={handlePageSizeChange}
            page={page}
            size={size}
            totalPages={totalPages}
            totalElements={totalElements}
          />
        </>
      )}
    </main>
  );
};
