import { Component, OnDestroy, OnInit } from '@angular/core';
import { IshtaService } from '../services/ishta.service';
import { Observable } from 'rxjs/internal/Observable';
import { Subscription, fromEvent } from 'rxjs';
import { AppSettings } from '../../../utils/app-settings';
import { DialogService } from '../../../utils/dialog.service';

@Component({
  selector: 'app-ishta',
  standalone: true,
  imports: [],
  templateUrl: './ishta.component.html',
  styleUrl: './ishta.component.scss',
})
export class IshtaComponent implements OnInit, OnDestroy {
  constructor(
    private service: IshtaService,
    private dialogService: DialogService
  ) {}

  testWelcome: string = 'Error: Text not found';
  testInternet: string = 'Error: Cant connect to httpbin.org/get';
  screenWidth: number = window.innerWidth;
  screenHeight: number = window.innerHeight;
  hideLoading: boolean = true;
  MIN_PC_SIZE = AppSettings.MIN_PC_SIZE;

  resizeObservable$!: Observable<Event>;
  resizeSubscription$!: Subscription;

  ngOnInit(): void {
    this.service.testApiWelcome().subscribe({
      next: (reponse: string) => {
        this.testWelcome = reponse;
      },
    });

    this.service.testInternet().subscribe({
      next: (reponse) => {
        if (reponse.status === 200) {
          this.testInternet = 'Connexion à httpbin.org/get OK';
        }
      },
    });

    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.subscribe(() => {
      this.screenWidth = window.innerWidth;
      this.screenHeight = window.innerHeight;
    });
  }

  ngOnDestroy() {
    this.resizeSubscription$.unsubscribe();
  }

  screenClass(width: number): string {
    return width > this.MIN_PC_SIZE ? 'mode PC' : 'mode Mobile';
  }

  testInputDialog(): void {
    this.dialogService
      .openInputDialog($localize`Souhaitez-vous écrire quelque chose ?`)
      .subscribe({
        next: (result) => {
          window.alert(result);
        },
      });
  }
}
