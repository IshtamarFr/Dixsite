import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { PicvidTitleComponent } from './picvid-title.component';
import { Picvid } from '../../interfaces/picvid.interface';

describe('PicvidTitleComponent', () => {
  let component: PicvidTitleComponent;
  let fixture: ComponentFixture<PicvidTitleComponent>;

  let mockPicvid: Picvid = {
    id: 66,
    name: 'Dixee',
    dateTimeExif: new Date('2023-02-12T21:14:44.000+00:00'),
    takenLocation: 'Dans le salon',
    fileLocation: 'HvrNV7V6eXax1K1.jpg',
    description: 'Dixee fait la belle',
    createdAt: new Date('2024-04-13T21:26:36.056964'),
    modifiedAt: new Date('2024-04-13T21:26:36.056964'),
    album_id: 6,
  };

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
    component.picvid = mockPicvid;
    component.isAdminTriggered = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
