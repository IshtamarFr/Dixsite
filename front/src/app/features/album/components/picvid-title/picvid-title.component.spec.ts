import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { PicvidTitleComponent } from './picvid-title.component';

describe('PicvidTitleComponent', () => {
  let component: PicvidTitleComponent;
  let fixture: ComponentFixture<PicvidTitleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PicvidTitleComponent,
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [],
    }).compileComponents();

    fixture = TestBed.createComponent(PicvidTitleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
