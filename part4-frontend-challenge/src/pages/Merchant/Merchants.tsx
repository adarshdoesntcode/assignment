import { Input } from "@/components/ui/input";
import "./Merchants.css";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import MerchantList from "./components/MerchantList";
import TablePagination from "@/components/common/TablePagination";
import { useState, useEffect } from "react";
import { DEFAULT_FILTERS, MerchantFilterState } from "@/types/merchant";
import { useMerchants } from "@/hooks/useMerchants";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

/**
 * Merchants Page Component
 * 
 * This page is a placeholder for future merchant management functionality.
 * 
 * ASSIGNMENT: Implement the following features:
 * 
 * 1. **Merchant List View**
 *    - Display all merchants in a table or card layout
 *    - Show merchant ID, name, status, and contact information
 *    - Add search/filter functionality
 * 
 * 2. **Merchant Details**
 *    - Click on a merchant to view detailed information
 *    - Show merchant settings, payment methods, and statistics
 * 
 * 3. **Add/Edit Merchant**
 *    - Form to create new merchants
 *    - Edit existing merchant information
 *    - Validate merchant data before submission
 * 
 * 4. **API Integration**
 *    - Create merchant service API calls
 *    - GET /api/v1/merchants - List all merchants
 *    - GET /api/v1/merchants/{id} - Get merchant details
 *    - POST /api/v1/merchants - Create new merchant
 *    - PUT /api/v1/merchants/{id} - Update merchant
 * 
 * 
 *  <h3>Merchant List <span style={{ backgroundColor: '#667eea', color: 'white', padding: '2px 8px', borderRadius: '8px', fontSize: '12px', marginLeft: '8px' }}>30 pts</span></h3>
            <p>View and search all registered merchants</p>
            <ul className="feature-list">
              <li>Display merchant information in table format (10 pts)</li>
              <li>Search and filter by name, ID, or status (5 pts)</li>
              <li>Sort by various criteria (5 pts)</li>
              <li>Pagination for large datasets (5 pts)</li>
              <li>Loading states and error handling (5 pts)</li>
            </ul>
          </div>

          <div className="feature-card">
            <div className="feature-icon">➕</div>
            <h3>Add New Merchant <span style={{ backgroundColor: '#667eea', color: 'white', padding: '2px 8px', borderRadius: '8px', fontSize: '12px', marginLeft: '8px' }}>25 pts</span></h3>
            <p>Register new merchants to the platform</p>
            <ul className="feature-list">
              <li>Form with merchant details (name, email, phone) (8 pts)</li>
              <li>Business information and registration (5 pts)</li>
              <li>Submit to POST /api/v1/merchants (5 pts)</li>
              <li>Input validation and error handling (4 pts)</li>
              <li>Success notifications and form reset (3 pts)</li>
            </ul>
          </div>

          <div className="feature-card">
            <div className="feature-icon">✏️</div>
            <h3>Edit Merchant Details <span style={{ backgroundColor: '#667eea', color: 'white', padding: '2px 8px', borderRadius: '8px', fontSize: '12px', marginLeft: '8px' }}>20 pts</span></h3>
            <p>Update merchant information and settings</p>
            <ul className="feature-list">
              <li>Pre-populate form with existing data (5 pts)</li>
              <li>Update contact details and address (5 pts)</li>
              <li>Manage merchant status (active/inactive) (5 pts)</li>
              <li>Submit to PUT /api/v1/merchants/:id (3 pts)</li>
              <li>Confirmation dialogs (2 pts)</li>
            </ul>
          </div>

          <div className="feature-card">
            <div className="feature-icon">🔍</div>
            <h3>Merchant Details View <span style={{ backgroundColor: '#667eea', color: 'white', padding: '2px 8px', borderRadius: '8px', fontSize: '12px', marginLeft: '8px' }}>25 pts</span></h3>
            <p>View comprehensive merchant information</p>
            <ul className="feature-list">
              <li>Display complete merchant profile (5 pts)</li>
              <li>Show transaction statistics (8 pts)</li>
              <li>List recent transactions (7 pts)</li>
              <li>View merchant activity timeline (3 pts)</li>
              <li>Export transaction history (2 pts)</li>
            </ul>
          </div>
        </div>

        <div className="technical-notes">
          <h2>📝 Technical Implementation Notes</h2>
          <div className="notes-content">
            <div className="note-section">
              <h3>API Endpoints to Implement:</h3>
              <ul>
                <li><code>GET /api/v1/merchants</code> - List all merchants (with pagination)</li>
                <li><code>GET /api/v1/merchants/{'{id}'}</code> - Get merchant details</li>
                <li><code>POST /api/v1/merchants</code> - Create new merchant</li>
                <li><code>PUT /api/v1/merchants/{'{id}'}</code> - Update merchant</li>
                <li><code>DELETE /api/v1/merchants/{'{id}'}</code> - Deactivate merchant</li>
              </ul>
            </div>

            <div className="note-section">
              <h3>Components to Create:</h3>
              <ul>
                <li><code>MerchantList.tsx</code> - Table/list view of merchants</li>
                <li><code>MerchantCard.tsx</code> - Individual merchant card component</li>
                <li><code>MerchantForm.tsx</code> - Form for add/edit merchant</li>
                <li><code>MerchantDetails.tsx</code> - Detailed merchant view</li>
                <li><code>MerchantFilters.tsx</code> - Search and filter controls</li>
              </ul>
            </div>

            <div className="note-section">
              <h3>State Management:</h3>
              <ul>
                <li>Create custom hook: <code>useMerchants()</code></li>
                <li>Manage merchant list state and loading states</li>
                <li>Handle form validation and submission</li>
                <li>Implement error handling and success notifications</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="getting-started">
          <h2>🚀 Getting Started</h2>
          <p>To implement this feature:</p>
          <ol>
            <li>Create the merchant type definitions in <code>src/types/merchant.ts</code></li>
            <li>Implement the merchant API service in <code>src/services/merchantService.ts</code></li>
            <li>Create the custom hook <code>src/hooks/useMerchants.ts</code></li>
            <li>Build the UI components in <code>src/components/merchants/</code></li>
            <li>Update this page to use the new components</li>
          </ol>
        </div>
      </div>


 */

export const Merchants = () => {
  const [searchValue, setSearchValue] = useState("");
  const [searchType, setSearchType] = useState<"merchantId" | "merchantName">(
    "merchantId"
  );
  const [filters, setFilters] = useState<MerchantFilterState>(DEFAULT_FILTERS);
  const [size, setSize] = useState(10);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const { data, loading, error } = useMerchants(filters);

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
      // Clear the filter
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
    setFilters(DEFAULT_FILTERS);
    setSearchValue("");
    setSearchType("merchantId");
  };

  return (
    <main className="container px-4 py-8 mx-auto space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 md:flex-row md:items-end">
        <div className="flex-1">
          <h1 className="mb-4 text-2xl font-bold">Merchant Dashboard</h1>
          <form onSubmit={handleSubmit} className="flex w-full gap-2 md:w-auto">
            <Select
              value={searchType}
              onValueChange={(value) =>
                setSearchType(value as "merchantId" | "merchantName")
              }
            >
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="Select" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="merchantId">Merchant ID</SelectItem>

                <SelectItem value="merchantName">Merchant Name</SelectItem>
              </SelectContent>
            </Select>
            <Input
              className="w-full md:w-[300px]"
              value={searchValue}
              placeholder={`Search by ${
                searchType === "merchantId" ? "Merchant ID" : "Merchant Name"
              }`}
              autoComplete="off"
              onChange={(e) => setSearchValue(e.target.value)}
            />
            <Button type="submit" size="default">
              Search
            </Button>
            {(filters.merchantId || filters.merchantName) && (
              <Button
                type="button"
                variant="outline"
                size="default"
                onClick={handleReset}
              >
                Reset
              </Button>
            )}
          </form>
        </div>
        <Button size="lg">New +</Button>
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
          <MerchantList
            merchants={data.merchants}
            sortBy={filters.sortBy}
            sortDirection={filters.sortDirection}
            onSort={handleSort}
          />

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
