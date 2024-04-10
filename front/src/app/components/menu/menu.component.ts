import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { SessionService } from '../../services/session.service';
import { Observable, Subscription, fromEvent } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { User } from '../../interfaces/user.interface';
import { AppSettings } from '../../utils/app-settings';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    MatIconModule,
    MatSidenavModule,
    MatDividerModule,
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class MenuComponent implements OnInit, OnDestroy {
  constructor(
    private router: Router,
    private sessionService: SessionService,
    private ngZone: NgZone
  ) {}

  screenWidth: number = window.innerWidth;
  resizeObservable$!: Observable<Event>;
  resizeSubscription$!: Subscription;
  user?: User;
  MIN_PC_SIZE = AppSettings.MIN_PC_SIZE;

  ngOnInit(): void {
    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.subscribe(() => {
      this.screenWidth = window.innerWidth;
    });
    this.user = this.sessionService.user;
  }

  ngOnDestroy(): void {
    this.resizeSubscription$.unsubscribe();
  }

  public logout(): void {
    this.sessionService.logOut();
    this.ngZone.run(() => {
      this.router.navigate(['']);
    });
  }
}
