import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
  PaginationEllipsis,
} from "@/components/ui/pagination";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";

interface TablePaginationProps {
  handlePageChange: (page: number) => void;
  handlePageSizeChange: (size: number) => void;
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

function TablePagination({
  handlePageChange,
  handlePageSizeChange,
  page,
  size,
  totalPages,
  totalElements,
}: TablePaginationProps) {
  // Calculate page range to display
  // Note: API uses 0-indexed pages, but we display 1-indexed to users
  const getPageNumbers = () => {
    const pages: (number | "ellipsis")[] = [];
    const maxPagesToShow = 5;
    const currentDisplayPage = page + 1; // Convert 0-indexed to 1-indexed for display

    if (totalPages <= maxPagesToShow) {
      // Show all pages if total is less than max
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(1);

      // Calculate start and end of middle pages
      let startPage = Math.max(2, currentDisplayPage - 1);
      let endPage = Math.min(totalPages - 1, currentDisplayPage + 1);

      // Adjust if we're near the start
      if (currentDisplayPage <= 3) {
        endPage = 4;
      }

      // Adjust if we're near the end
      if (currentDisplayPage >= totalPages - 2) {
        startPage = totalPages - 3;
      }

      // Add ellipsis after first page if needed
      if (startPage > 2) {
        pages.push("ellipsis");
      }

      // Add middle pages
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }

      // Add ellipsis before last page if needed
      if (endPage < totalPages - 1) {
        pages.push("ellipsis");
      }

      // Always show last page
      pages.push(totalPages);
    }

    return pages;
  };

  const startItem = totalElements === 0 ? 0 : page * size + 1;
  const endItem = Math.min((page + 1) * size, totalElements);

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-4">
      {/* Results info */}
      <div className="text-sm text-muted-foreground">
        Showing {startItem} to {endItem} of {totalElements} results
      </div>

      {/* Pagination controls */}
      <div className="flex items-center gap-4">
        {/* Page size selector */}
        <div className="flex items-center gap-2">
          <span className="text-sm text-foreground whitespace-nowrap">
            Rows per page:
          </span>
          <Select
            value={size.toString()}
            onValueChange={(value) => handlePageSizeChange(Number(value))}
          >
            <SelectTrigger className="w-[70px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="10">10</SelectItem>
              <SelectItem value="20">20</SelectItem>
              <SelectItem value="50">50</SelectItem>
              <SelectItem value="100">100</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Page navigation */}
        {totalPages > 1 && (
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    if (page > 0) handlePageChange(page - 1);
                  }}
                  className={page <= 0 ? "pointer-events-none opacity-50" : ""}
                />
              </PaginationItem>

              {getPageNumbers().map((pageNum, idx) => (
                <PaginationItem key={idx}>
                  {pageNum === "ellipsis" ? (
                    <PaginationEllipsis />
                  ) : (
                    <PaginationLink
                      href="#"
                      onClick={(e) => {
                        e.preventDefault();
                        // Convert 1-indexed display page to 0-indexed API page
                        handlePageChange(pageNum - 1);
                      }}
                      isActive={page === pageNum - 1}
                    >
                      {pageNum}
                    </PaginationLink>
                  )}
                </PaginationItem>
              ))}

              <PaginationItem>
                <PaginationNext
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    if (page < totalPages - 1) handlePageChange(page + 1);
                  }}
                  className={
                    page >= totalPages - 1
                      ? "pointer-events-none opacity-50"
                      : ""
                  }
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        )}
      </div>
    </div>
  );
}

export default TablePagination;
