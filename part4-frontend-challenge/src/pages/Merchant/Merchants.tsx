import { Input } from "@/components/ui/input";
import "./Merchants.css";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import MerchantList from "./components/MerchantList";
import TablePagination from "@/components/common/TablePagination";
import { useState, useEffect } from "react";
import { ACTIVE_FILTERS, MerchantFilterState } from "@/types/merchant";
import { useMerchants } from "@/hooks/useMerchants";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AddMerchant } from "./components/AddMerchant";


export const Merchants = () => {
  const [searchValue, setSearchValue] = useState("");
  const [searchType, setSearchType] = useState<"merchantId" | "merchantName">(
    "merchantId"
  );
  const [tab, setTab] = useState("active");
  const [filters, setFilters] = useState<MerchantFilterState>(ACTIVE_FILTERS);
  const [size, setSize] = useState(10);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const { data, loading, error, refetch } = useMerchants(filters);

  useEffect(() => {
    if (data && !error) {
      setTotalPages(data.pagination.totalPages);
      setPage(data.pagination.page);
      setSize(data.pagination.size);
      setTotalElements(data.pagination.totalElements);
    }
  }, [data, error]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const search = searchValue.trim();

    if (!search) {
      const newFilters = { ...filters };
      delete newFilters.merchantId;
      delete newFilters.merchantName;
      setFilters({ ...newFilters, page: 0 });
      return;
    }

    if (searchType === "merchantId") {
      setFilters({
        ...filters,
        merchantId: search.toUpperCase(),
        merchantName: undefined,
        page: 0,
      });
    } else {
      setFilters({
        ...filters,
        merchantName: search,
        merchantId: undefined,
        page: 0,
      });
    }
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    setFilters({ ...filters, page: newPage });
  };

  const handlePageSizeChange = (newSize: number) => {
    setSize(newSize);
    setFilters({ ...filters, size: newSize, page: 0 });
  };

  const handleSort = (field: string) => {
    const currentSortBy = filters.sortBy || "";
    const currentSortDirection = filters.sortDirection || "";

    const sortFields = currentSortBy.split(",").filter((f: string) => f);
    const sortDirections = currentSortDirection
      .split(",")
      .filter((d: string) => d);

    const fieldIndex = sortFields.indexOf(field);

    if (fieldIndex === -1) {
      sortFields.push(field);
      sortDirections.push("ASC");
    } else {
      if (sortDirections[fieldIndex] === "ASC") {
        sortDirections[fieldIndex] = "DESC";
      } else {
        sortFields.splice(fieldIndex, 1);
        sortDirections.splice(fieldIndex, 1);
      }
    }

    setFilters({
      ...filters,
      sortBy: sortFields.join(",") || undefined,
      sortDirection: sortDirections.join(",") || undefined,
      page: 0,
    });
  };

  const handleReset = () => {
    setFilters(ACTIVE_FILTERS);
    setSearchValue("");
    setTab("active");
    setSearchType("merchantId");
  };

  return (
    <main className="container px-4 py-8 mx-auto space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 md:flex-row md:items-end">
        <div className="flex-1">
          <h1 className="text-2xl font-bold ">Merchant Dashboard</h1>
          <p className="text-sm text-muted-foreground">All the merchants</p>
        </div>
      </div>

      {loading && (
        <div className="w-full flex items-center justify-center h-[500px]">
          <Spinner />
        </div>
      )}

      {error && (
        <div className="p-8 text-center text-red-600 border border-red-200 rounded-lg bg-red-50">
          <p className="font-semibold">Error loading merchants</p>
          <p className="mt-2 text-sm">{error.message}</p>
        </div>
      )}

      {data && !loading && (
        <>
          <Tabs
            value={tab}
            onValueChange={(value: string) => {
              setTab(value);
              if (value === "inactive") {
                setFilters((prev) => {
                  return {
                    ...prev,
                    isActive: false,
                  };
                });
              } else if (value === "active") {
                setFilters((prev) => {
                  return {
                    ...prev,
                    isActive: true,
                  };
                });
              }
            }}
          >
            <div className="flex flex-col items-stretch justify-between gap-4 mb-2 md:flex-row md:items-center">
              <div className="flex flex-col items-stretch gap-4 md:flex-row md:items-center">
                <TabsList>
                  <TabsTrigger value="active">Active</TabsTrigger>
                  <TabsTrigger value="inactive">Not Active</TabsTrigger>
                </TabsList>
                <form
                  onSubmit={handleSubmit}
                  className="flex flex-col w-full gap-2 sm:flex-row md:w-auto"
                >
                  <Select
                    value={searchType}
                    onValueChange={(value) =>
                      setSearchType(value as "merchantId" | "merchantName")
                    }
                  >
                    <SelectTrigger className="w-full sm:w-[180px]">
                      <SelectValue placeholder="Select" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="merchantId">Merchant ID</SelectItem>

                      <SelectItem value="merchantName">
                        Merchant Name
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <Input
                    className="w-full sm:w-auto md:w-[300px]"
                    value={searchValue}
                    placeholder={`Search by ${searchType === "merchantId"
                      ? "Merchant ID"
                      : "Merchant Name"
                      }`}
                    autoComplete="off"
                    onChange={(e) => setSearchValue(e.target.value)}
                  />
                  <div className="flex gap-2">
                    <Button
                      type="submit"
                      size="default"
                      variant={"outline"}
                      className="flex-1 sm:flex-auto"
                    >
                      Search
                    </Button>
                    {(filters.merchantId || filters.merchantName) && (
                      <Button
                        type="button"
                        variant="outline"
                        size="default"
                        onClick={handleReset}
                        className="flex-1 sm:flex-auto"
                      >
                        Reset
                      </Button>
                    )}
                  </div>
                </form>
              </div>

              <AddMerchant onSuccess={refetch} />
            </div>
            <TabsContent value="active">
              <MerchantList
                merchants={data.merchants}
                sortBy={filters.sortBy}
                sortDirection={filters.sortDirection}
                onSort={handleSort}
              />
            </TabsContent>
            <TabsContent value="inactive">
              <MerchantList
                merchants={data.merchants}
                sortBy={filters.sortBy}
                sortDirection={filters.sortDirection}
                onSort={handleSort}
              />
            </TabsContent>
          </Tabs>

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
