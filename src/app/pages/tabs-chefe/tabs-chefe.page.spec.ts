import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TabsChefePage } from './tabs-chefe.page';

describe('TabsChefePage', () => {
  let component: TabsChefePage;
  let fixture: ComponentFixture<TabsChefePage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TabsChefePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
