import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AlbumListComponent } from '../album-list/album-list.component';
import { FormMultiplePicvidsComponent } from './form-multiple-picvids.component';
import { RouterModule } from '@angular/router';
import '@angular/localize/init';

describe('FormMultiplePicvidsComponent', () => {
  let component: FormMultiplePicvidsComponent;
  let fixture: ComponentFixture<FormMultiplePicvidsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AlbumListComponent,
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [],
    }).compileComponents();

    fixture = TestBed.createComponent(FormMultiplePicvidsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
