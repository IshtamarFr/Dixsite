import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../../../features/auth/services/auth.service';
import { Subscription, timer } from 'rxjs';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-maintenance',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './maintenance.component.html',
  styleUrl: './maintenance.component.scss',
})
export class MaintenanceComponent implements OnInit, OnDestroy {
  subscription$!: Subscription;

  constructor(
    private authService: AuthService,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscription$ = timer(0, 5000).subscribe(() => {
      this.authService.verifyBackend().subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.router.navigate(['']);
          });
        },
      });
    });
  }

  ngOnDestroy(): void {
    this.subscription$.unsubscribe();
  }
}
