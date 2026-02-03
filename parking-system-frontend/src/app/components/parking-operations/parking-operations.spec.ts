import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingOperations } from './parking-operations';

describe('ParkingOperations', () => {
  let component: ParkingOperations;
  let fixture: ComponentFixture<ParkingOperations>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingOperations]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParkingOperations);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
