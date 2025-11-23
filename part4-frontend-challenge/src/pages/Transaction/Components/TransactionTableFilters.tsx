import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Calendar } from "@/components/ui/calendar";
import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { CalendarIcon, X, Filter } from "lucide-react";
import { format } from "date-fns";
import { useState, useEffect } from "react";
import type { DateRange } from "react-day-picker";

interface TransactionTableFiltersProps {
  onApplyFilters: (status: string, startDate: string, endDate: string) => void;
  handleReset: () => void;
  startDate: string;
  endDate: string;
  status: string;
}

function TransactionTableFilters({
  onApplyFilters,
  startDate,
  endDate,
  status,
  handleReset,
}: TransactionTableFiltersProps) {
  const [localStatus, setLocalStatus] = useState(status);
  const [dateRange, setDateRange] = useState<DateRange | undefined>({
    from: startDate ? new Date(startDate) : undefined,
    to: endDate ? new Date(endDate) : undefined,
  });

  useEffect(() => {
    setLocalStatus(status);
    setDateRange({
      from: startDate ? new Date(startDate) : undefined,
      to: endDate ? new Date(endDate) : undefined,
    });
  }, [status, startDate, endDate]);

  const handleDateSelect = (range: DateRange | undefined) => {
    setDateRange(range);
  };

  const handleStatusSelect = (value: string) => {
    setLocalStatus(value);
  };

  const applyFilters = () => {
    const start = dateRange?.from
      ? dateRange.from.toISOString().split("T")[0]
      : "";
    const end = dateRange?.to ? dateRange.to.toISOString().split("T")[0] : "";
    onApplyFilters(localStatus, start, end);
  };

  const clearFilters = () => {
    setDateRange({ from: undefined, to: undefined });
    setLocalStatus("");
    handleReset();
  };

  const hasLocalFilters = localStatus || dateRange?.from || dateRange?.to;
  const hasActiveFilters = status || startDate || endDate;

  return (
    <div className="flex flex-col gap-4 mb-6 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-col gap-4 sm:flex-row sm:flex-1">
        <div className="w-full sm:w-auto">
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                className="justify-start w-full font-normal text-left sm:w-[300px]"
              >
                <CalendarIcon className="w-4 h-4 mr-2" />
                {dateRange?.from ? (
                  dateRange?.to ? (
                    <>
                      {format(dateRange.from, "MMM dd, yyyy")} -{" "}
                      {format(dateRange.to, "MMM dd, yyyy")}
                    </>
                  ) : (
                    format(dateRange.from, "MMM dd, yyyy")
                  )
                ) : (
                  <span className="text-muted-foreground">
                    Pick a date range
                  </span>
                )}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="start">
              <Calendar
                mode="range"
                selected={dateRange}
                onSelect={handleDateSelect}
                numberOfMonths={2}
              />
            </PopoverContent>
          </Popover>
        </div>
        <div className="w-full sm:w-[200px]">
          <Select value={localStatus} onValueChange={handleStatusSelect}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Select Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="completed">Completed</SelectItem>
              <SelectItem value="pending">Pending</SelectItem>
              <SelectItem value="failed">Failed</SelectItem>
              <SelectItem value="reversed">Reversed</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="flex gap-2">
        <Button
          onClick={applyFilters}
          disabled={!hasLocalFilters}
          variant={"secondary"}
        >
          <Filter className="w-4 h-4" />
          Apply Filters
        </Button>
        {hasActiveFilters && (
          <Button variant="outline" onClick={clearFilters}>
            <X className="w-4 h-4 " />
            Clear
          </Button>
        )}
      </div>
    </div>
  );
}

export default TransactionTableFilters;
