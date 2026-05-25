import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, of, shareReplay } from 'rxjs';

interface CountriesNowResponse<T> {
  error?: boolean;
  msg?: string;
  data?: T;
}

interface CountriesNowCountry {
  name: string;
}

interface CountriesNowState {
  name: string;
}

interface CountriesNowStatesData {
  states: CountriesNowState[];
}

@Injectable({ providedIn: 'root' })
export class LocationCatalogService {
  private readonly baseUrl = 'https://countriesnow.space/api/v0.1';

  private countries$?: Observable<string[]>;
  private readonly departmentsByCountry = new Map<string, Observable<string[]>>();
  private readonly citiesByCountryAndDepartment = new Map<string, Observable<string[]>>();

  constructor(private readonly http: HttpClient) {}

  getCountries(): Observable<string[]> {
    if (!this.countries$) {
      this.countries$ = this.http
        .get<CountriesNowResponse<CountriesNowCountry[]>>(`${this.baseUrl}/countries/iso`)
        .pipe(
          map((res) => (res?.data ?? []).map((c) => c.name).filter(Boolean)),
          map((names) => [...new Set(names)].sort((a, b) => a.localeCompare(b, undefined, { sensitivity: 'base' }))),
          shareReplay({ bufferSize: 1, refCount: false })
        );
    }

    return this.countries$;
  }

  getDepartments(country: string): Observable<string[]> {
    const key = (country ?? '').trim();
    if (!key) return of([]);

    const cached = this.departmentsByCountry.get(key);
    if (cached) return cached;

    const request$ = this.http
      .post<CountriesNowResponse<CountriesNowStatesData>>(`${this.baseUrl}/countries/states`, { country: key })
      .pipe(
        map((res) => (res?.data?.states ?? []).map((s) => s.name).filter(Boolean)),
        map((names) => [...new Set(names)].sort((a, b) => a.localeCompare(b, undefined, { sensitivity: 'base' }))),
        shareReplay({ bufferSize: 1, refCount: false })
      );

    this.departmentsByCountry.set(key, request$);
    return request$;
  }

  getCities(country: string, department: string): Observable<string[]> {
    const countryKey = (country ?? '').trim();
    const departmentKey = (department ?? '').trim();
    if (!countryKey || !departmentKey) return of([]);

    const cacheKey = `${countryKey}||${departmentKey}`;
    const cached = this.citiesByCountryAndDepartment.get(cacheKey);
    if (cached) return cached;

    const request$ = this.http
      .post<CountriesNowResponse<string[]>>(`${this.baseUrl}/countries/state/cities`, {
        country: countryKey,
        state: departmentKey,
      })
      .pipe(
        map((res) => (res?.data ?? []).filter(Boolean)),
        map((names) => [...new Set(names)].sort((a, b) => a.localeCompare(b, undefined, { sensitivity: 'base' }))),
        shareReplay({ bufferSize: 1, refCount: false })
      );

    this.citiesByCountryAndDepartment.set(cacheKey, request$);
    return request$;
  }
}
