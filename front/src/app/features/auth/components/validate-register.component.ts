import { Component, NgZone, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { take } from 'rxjs';
import { User } from '../../../interfaces/user.interface';
import { AuthSuccess } from '../interfaces/auth-success.interface';
import { SessionService } from '../../../services/session.service';
import { DialogService } from '../../../utils/dialog.service';

@Component({
  selector: 'app-validate-register',
  standalone: true,
  imports: [],
  template: '',
})
export class ValidateRegisterComponent implements OnInit {
  id: number = 0;
  token: string = 'unknown';

  constructor(
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private ngZone: NgZone,
    private router: Router,
    private sessionService: SessionService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.id = +this.activatedRoute.snapshot.queryParams['id'];
    this.token = this.activatedRoute.snapshot.queryParams['token'];

    this.authService
      .validateRegister(this.id, this.token)
      .pipe(take(1))
      .subscribe({
        next: (response: AuthSuccess) => {
          localStorage.setItem('token', response.token);
          this.authService.me().subscribe((user: User) => {
            this.sessionService.logIn(user);
            this.ngZone.run(() => {
              this.router.navigate(['main']);
            });
          });
        },
        error: (_) => {
          this.ngZone.run(() => {
            this.router.navigate(['/']).then(() => {
              this.dialogService
                .openConfirmDialog(
                  'La validation a échoué. Veuillez nous contacter si nécessaire.',
                  false
                )
                .subscribe();
            });
          });
        },
      });
  }
}
