import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotManagedModsComponent } from './not-managed-mods.component';

describe('NotManagedModsComponent', () => {
  let component: NotManagedModsComponent;
  let fixture: ComponentFixture<NotManagedModsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotManagedModsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotManagedModsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
