import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MathApiService } from '../../services/math-api.service';
import { MathComponent } from './math.component';

describe('MathComponent', () => {
  let fixture: ComponentFixture<MathComponent>;
  let component: MathComponent;
  let arithmetic: jasmine.Spy;

  beforeEach(() => {
    arithmetic = jasmine.createSpy('arithmetic').and.returnValue(of(7));
    TestBed.configureTestingModule({
      imports: [MathComponent],
      providers: [{
        provide: MathApiService,
        useValue: { arithmetic },
      }],
    });
    fixture = TestBed.createComponent(MathComponent);
    component = fixture.componentInstance;
  });

  it('sends valid arithmetic inputs and displays the backend result', () => {
    component.operation = 'ADD';
    component.firstValue = '4';
    component.secondValue = '3';

    component.execute();

    expect(arithmetic).toHaveBeenCalledWith('ADD', 4, 3);
    expect(component.resultText).toBe('7');
    expect(component.errorMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('rejects invalid numeric input before making a request', () => {
    component.firstValue = 'not-a-number';

    component.execute();

    expect(arithmetic).not.toHaveBeenCalled();
    expect(component.errorMessage).toContain('finite number');
    expect(component.loading).toBeFalse();
  });
});
