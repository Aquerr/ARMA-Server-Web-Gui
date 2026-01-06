import { WritableSignal } from "@angular/core";

export function moveItemBetweenSignalLists<T>(
  oldList: WritableSignal<T[]>,
  newList: WritableSignal<T[]>,
  movedItem: T
): void {
  const itemIndex = oldList().findIndex((item) => item == movedItem);
  if (itemIndex > -1) {
    oldList.update((oldMissions) => {
      return oldMissions.filter((item, index) => index != itemIndex);
    });
  }
  newList.update((oldItems) => {
    oldItems.push(movedItem);
    return oldItems;
  });
}
