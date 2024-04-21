import { AfterContentInit, Component, NgZone, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './features/auth/services/auth.service';
import { User } from './interfaces/user.interface';
import { SessionService } from './services/session.service';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { FooterComponent } from './components/footer/footer.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FooterComponent, MatButtonModule, MatIconModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, AfterContentInit {
  title = 'DixsiteFront';

  constructor(
    private authService: AuthService,
    private router: Router,
    private sessionService: SessionService,
    private ngZone: NgZone
  ) {
    registerLocaleData(localeFr, 'fr');
  }

  public ngOnInit(): void {
    this.autoLog();
  }

  ngAfterContentInit(): void {
    this.checkConnection();
  }

  public $isLogged(): Observable<boolean> {
    return this.sessionService.$isLogged();
  }

  public autoLog(): void {
    this.authService.me().subscribe({
      next: (user: User) => {
        this.sessionService.logIn(user);
        this.ngZone.run(() => {
          this.router.navigate(['main']);
        });
      },
      error: () => {
        this.sessionService.logOut();
      },
    });
  }

  private checkConnection(): void {
    this.authService.verifyBackend().subscribe({
      error: () => {
        this.ngZone.run(() => {
          this.router.navigate(['maintenance']);
        });
      },
    });
  }

  scrollToTop(): void {
    document.getElementById('topPage')?.scrollIntoView({ behavior: 'smooth' });
  }
}
