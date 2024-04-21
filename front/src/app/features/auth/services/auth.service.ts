import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';
import { AuthSuccess } from '../interfaces/auth-success.interface';
import { LoginRequest } from '../interfaces/login-request.interface';
import { RegisterRequest } from '../interfaces/register-request.interface';
import { ModifyUserRequest } from '../interfaces/modify-user-request.interface';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private pathService = 'api/auth';

  constructor(private httpClient: HttpClient) {}

  public register(
    registerRequest: RegisterRequest,
    language: string
  ): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/register`,
      registerRequest,
      { params: { language }, responseType: 'text' }
    );
  }

  public update(modifyUserRequest: ModifyUserRequest): Observable<AuthSuccess> {
    return this.httpClient.put<AuthSuccess>(
      `${this.pathService}/me`,
      modifyUserRequest
    );
  }

  public login(loginRequest: LoginRequest): Observable<AuthSuccess> {
    return this.httpClient.post<AuthSuccess>(
      `${this.pathService}/login`,
      loginRequest
    );
  }

  public verifyBackend(): Observable<string> {
    return this.httpClient.get(`${this.pathService}/welcome`, {
      responseType: 'text',
    });
  }

  public me(): Observable<User> {
    return this.httpClient.get<User>(`${this.pathService}/me`);
  }

  public forgotten(email: string, language: string): Observable<string> {
    return this.httpClient.post(`${this.pathService}/forgotten`, null, {
      params: { email, language },
      responseType: 'text',
    });
  }

  public validateRegister(id: number, token: string): Observable<AuthSuccess> {
    return this.httpClient.put<AuthSuccess>(
      `${this.pathService}/validate`,
      null,
      {
        params: { id, token },
      }
    );
  }
}
