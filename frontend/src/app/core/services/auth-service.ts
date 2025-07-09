import {inject, Injectable} from '@angular/core';
import {environment} from '../../../enviroments/enviroment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Register} from '../models/register';
import {LoginRequest} from '../models/login-request';
import {LoginResponse} from '../models/login-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _baseUrl: string = `${environment.apiBaseUrl}/auth`;

  constructor() {
  }

  private http: HttpClient = inject(HttpClient);

  createUser(payload: Register): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this._baseUrl}/register`, payload, {observe: 'response'});
  }

  loginUser(payload: LoginRequest): Observable<HttpResponse<LoginResponse>> {
    return this.http.post<LoginResponse>(`${this._baseUrl}/login`, payload, {observe: 'response'});
  }

  logoutUser(): Observable<HttpResponse<string>> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No token available');
    }

    return this.http.post(
      `${this._baseUrl}/logout`, {}, {observe: 'response', headers: {Authorization: `Bearer ${token}`}, responseType: 'text'}
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.clear();
  }
}
