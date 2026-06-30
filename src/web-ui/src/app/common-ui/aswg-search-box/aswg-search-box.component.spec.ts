import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AswgSearchBoxComponent } from './aswg-search-box.component';

describe('AswgSearchBoxComponent', () => {
  let component: AswgSearchBoxComponent;
  let fixture: ComponentFixture<AswgSearchBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgSearchBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AswgSearchBoxComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
