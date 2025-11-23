import { Card, CardContent } from "@/components/ui/card";
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
import { CalendarIcon, X } from "lucide-react";
import { format } from "date-fns";
import { useState } from "react";
import type { DateRange } from "react-day-picker";

interface TransactionTableFiltersProps {
  handleStatusChange: (status: string) => void;
  handleDateRangeChange: (startDate: string, endDate: string) => void;
  handleReset: () => void;
  startDate: string;
  endDate: string;
  status: string;
}

function TransactionTableFilters({
  handleStatusChange,
  handleDateRangeChange,
  startDate,
  endDate,
  status,
  handleReset,
}: TransactionTableFiltersProps) {
  const [dateRange, setDateRange] = useState<DateRange | undefined>({
    from: startDate ? new Date(startDate) : undefined,
    to: endDate ? new Date(endDate) : undefined,
  });

  const handleDateSelect = (range: DateRange | undefined) => {
    if (range) {
      setDateRange(range);
      if (range.from && range.to) {
        handleDateRangeChange(
          range.from.toISOString().split("T")[0],
          range.to.toISOString().split("T")[0]
        );
      }
    }
  };

  const handleStatusSelect = (value: string) => {
    handleStatusChange(value === "all" ? "" : value);
  };

  const clearFilters = () => {
    setDateRange({ from: undefined, to: undefined });
    handleReset();
  };

  const hasActiveFilters = status || startDate || endDate;

  return (
    <Card className="mb-5">
      <CardContent>
        <div className="flex flex-col gap-4 sm:flex-row">
          <div className="flex-1">
            <Select value={status} onValueChange={handleStatusSelect}>
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

          <div className="flex-1">
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className="justify-start w-full font-normal text-left"
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
                    <span>Pick a date range</span>
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
          {hasActiveFilters && (
            <Button
              variant="ghost"
              size="sm"
              onClick={clearFilters}
              className="h-8 px-2 lg:px-3"
            >
              <X className="w-4 h-4 mr-2" />
              Clear filters
            </Button>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

export default TransactionTableFilters;
