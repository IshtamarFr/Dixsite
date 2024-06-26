import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { User } from '../../../../interfaces/user.interface';
import { AuthSuccess } from '../../interfaces/auth-success.interface';
import '@angular/localize/init';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;

  const mockAuthSuccess: AuthSuccess = {
    token: 'azertyuiop',
  };

  const mockUser: User = {
    id: 1,
    name: 'Ishta',
    email: 'test@testing.test',
    roles: 'ROLE_USER',
    maxAlbums: 10,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        HttpClientTestingModule,
        NoopAnimationsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('get back should call component method', () => {
    //Given
    let componentSpyBack = jest.spyOn(component, 'back');

    //When
    component.back();

    //Then
    expect(componentSpyBack).toBeDefined();
    expect(componentSpyBack).toHaveBeenCalled();
  });

  it('submit should call service', () => {
    //Given
    let authServiceSpyLogin = jest
      .spyOn(authService, 'register')
      .mockReturnValue(of('all is fine'));

    //When
    component.submit();

    //Then
    expect(authServiceSpyLogin).toHaveBeenCalled();
  });

  it('should set onError to true if submitted data is erroneous', () => {
    //Given
    let authServiceSpy = jest
      .spyOn(authService, 'register')
      .mockReturnValue(throwError(() => new Error()));

    //When
    component.submit();

    //Then
    expect(authServiceSpy).toHaveBeenCalled();
    expect(component['onError']).toBe(true);
  });
});
