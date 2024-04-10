import { Component, NgZone } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NgIf } from '@angular/common';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { SessionService } from '../../../../services/session.service';
import { AuthSuccess } from '../../interfaces/auth-success.interface';
import { LoginRequest } from '../../interfaces/login-request.interface';
import { User } from '../../../../interfaces/user.interface';
import { DialogService } from '../../../../utils/dialog.service';
import { UtilityService } from '../../../../utils/utility.service';
import { take } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    NgIf,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  public hide = true;
  public onError = false;

  public form = this.fb.group({
    email: [
      '',
      [Validators.required, Validators.email, Validators.maxLength(63)],
    ],
    password: [
      '',
      [Validators.required, Validators.minLength(8), Validators.maxLength(60)],
    ],
  });

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private sessionService: SessionService,
    private dialogService: DialogService,
    private utilityService: UtilityService,
    private ngZone: NgZone
  ) {}

  public submit(): void {
    const loginRequest = this.form.value as LoginRequest;
    this.authService.login(loginRequest).subscribe({
      next: (response: AuthSuccess) => {
        localStorage.setItem('token', response.token);
        this.authService.me().subscribe((user: User) => {
          this.sessionService.logIn(user);
          this.ngZone.run(() => {
            this.router.navigate(['main']);
          });
        });
      },
      error: () => (this.onError = true),
    });
  }

  public back(): void {
    window.history.back();
  }

  public forgotten(): void {
    this.dialogService
      .openInputDialog('Veuillez entrer votre adresse email')
      .subscribe({
        next: (response) => {
          if (response != undefined) {
            if (this.utilityService.isEmail(response)) {
              //User has input a potentially valid email address
              this.authService
                .forgotten(response)
                .pipe(take(1))
                .subscribe(() => {
                  this.dialogService
                    .openConfirmDialog(
                      "Un email vient d'être envoyé à l'adresse " + response,
                      false
                    )
                    .subscribe();
                });
            }
          }
        },
      });
  }
}
