import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LotLayout } from './lot-layout';

describe('LotLayout', () => {
  let component: LotLayout;
  let fixture: ComponentFixture<LotLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LotLayout]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LotLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
