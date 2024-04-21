import { NgIf } from '@angular/common';
import { Component, NgZone, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { RegisterRequest } from '../../interfaces/register-request.interface';
import { AuthService } from '../../services/auth.service';
import { DialogService } from '../../../../utils/dialog.service';

@Component({
  selector: 'app-register',
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
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
  public hide = true;
  public onError = false;
  public hideLoading = true;

  public language: string = 'en';

  ngOnInit(): void {
    if (window.location.href.includes('/fr-FR/')) this.language = 'fr';
  }

  checkPasswords: ValidatorFn = (
    group: AbstractControl
  ): ValidationErrors | null => {
    let pass = group.get('password')?.value;
    let confirmPass = group.get('password2')?.value;
    return pass === confirmPass ? null : { notSame: true };
  };

  public form = this.fb.group(
    {
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(63)],
      ],
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(30),
        ],
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.pattern('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,60}$'),
        ],
      ],
      password2: [
        '',
        [
          Validators.required,
          Validators.pattern('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,60}$'),
        ],
      ],
    },
    { validators: this.checkPasswords }
  );

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private ngZone: NgZone,
    private dialogService: DialogService
  ) {}

  public submit(): void {
    this.hideLoading = false;
    let temp = this.form.value;
    delete temp.password2;
    const registerRequest = temp as RegisterRequest;
    this.authService.register(registerRequest, this.language).subscribe({
      next: () => {
        this.hideLoading = true;
        this.dialogService
          .openConfirmDialog(
            $localize`Veuillez valider votre inscription en cliquant sur le lien envoyé par mail`,
            false
          )
          .subscribe({
            next: () => {
              this.ngZone.run(() => {
                this.router.navigate(['']);
              });
            },
          });
      },
      error: () => {
        this.onError = true;
        this.hideLoading = true;
      },
    });
  }

  public back() {
    window.history.back();
  }
}
