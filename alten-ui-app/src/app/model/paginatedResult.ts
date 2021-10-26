export interface PaginatedResult<T>{
    data: T[];
    pageSize: number;
    pageNumber: number;
    totalRecords: number;
}