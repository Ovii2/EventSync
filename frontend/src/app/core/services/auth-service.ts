import {inject, Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Register} from '../models/register';
import {LoginRequest} from '../models/login-request';
import {LoginResponse} from '../models/login-response';
import {jwtDecode} from 'jwt-decode';
import {JwtPayload} from '../models/jwt-payload';
import {UserRole} from '../models/enums/user-role';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _baseUrl: string = `${environment.apiBaseUrl}/auth`;


  constructor() {
  }

  private http: HttpClient = inject(HttpClient);
  private router: Router = inject(Router);

  createUser(payload: Register): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this._baseUrl}/register`, payload, {observe: 'response'});
  }

  loginUser(payload: LoginRequest): Observable<HttpResponse<LoginResponse>> {
    return this.http.post<LoginResponse>(`${this._baseUrl}/login`, payload, {observe: 'response'});
  }

  logoutUser(): Observable<HttpResponse<string>> {
    const token = this.getToken();
    if (!token) {
      this.logout();
      throw new Error('No token available');
    }

    return this.http.post(
      `${this._baseUrl}/logout`, {}, {observe: 'response', headers: {Authorization: `Bearer ${token}`}, responseType: 'text'}
    );
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    if (token && this.isTokenExpired(token)) {
      this.logout();
      return null;
    }
    return token;
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  isTokenExpired(token: string): boolean {
    try {
      const decoded: JwtPayload = jwtDecode(token);
      const currentTime: number = Date.now() / 1000;
      return decoded.exp < currentTime;
    } catch (error) {
      return true;
    }
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token;
  }

  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const decoded: JwtPayload = jwtDecode(token);
      return decoded.role?.includes(UserRole.ROLE_ADMIN);
    } catch (error) {
      throw new Error('Failed to decode token:');
    }
  }

  logout(reason: 'expired' | 'manual' = 'manual'): void {
    localStorage.removeItem('token');
    localStorage.clear();
    void this.router.navigate(['/login']);
  }
}
