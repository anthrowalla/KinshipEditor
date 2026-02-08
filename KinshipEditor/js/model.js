// Data model: Person, Marriage, Kind
// Port of Person.java, Marriage.java, Kind.java

export const NOLABEL = 0;
export const INITIALS = 1;
export const FIRST = 2;
export const LAST = 3;
export const WHOLE = 4;

export const SEX_FEMALE = 'Female';
export const SEX_MALE = 'Male';
export const SEX_NEUTER = 'Neuter';

// Global state
export const state = {
  folks: [],       // Person[]
  folkIndex: -1,
  knots: [],       // Marriage[]
  knotIndex: -1,
  idCounter: 0,
  refYear: '',
  doLabel: NOLABEL,
  symbolSize: 16,
};

export function resetState() {
  state.folks = [];
  state.folkIndex = -1;
  state.knots = [];
  state.knotIndex = -1;
  state.idCounter = 0;
  state.refYear = '';
}

// Person
export class Person {
  constructor(sex, x, y) {
    this.myId = ++state.idCounter;
    this.sex = sex;
    this.location = { x, y };
    this.name = 'Person ' + this.myId;
    this.comment = 'No Comment';
    this.yob = 'NA';
    this.yobq = '9';
    this.yod = 'NA';
    this.yodq = '9';
    this.marriages = [];      // Marriage[]
    this.parents = null;       // Marriage or null
    this.parentalUnions = [];  // Marriage[]
    this.selected = false;
    this.drawn = false;
  }

  get size() { return state.symbolSize; }

  bounds() {
    return { x: this.location.x, y: this.location.y, width: this.size, height: this.size };
  }

  bottomHinge() {
    return { x: this.location.x + this.size / 2, y: this.location.y + this.size };
  }

  topHinge() {
    return { x: this.location.x + this.size / 2, y: this.location.y };
  }

  setLocation(x, y) {
    this.location.x = x;
    this.location.y = y;
  }

  addMarriage(m) {
    if (!this.marriages.includes(m)) this.marriages.push(m);
    if (!m.spouses.includes(this)) m.spouses.push(this);
  }

  delMarriage(m) {
    this.marriages = this.marriages.filter(x => x !== m);
    m.spouses = m.spouses.filter(x => x !== this);
  }

  setParents(m) {
    if (m === null) {
      this.parents = null;
      return;
    }
    if (!this.parentalUnions.includes(m)) this.parentalUnions.push(m);
    this.parents = m;
    if (!m.sibset.includes(this)) m.addSib(this);
  }

  delParents(m) {
    this.parentalUnions = this.parentalUnions.filter(x => x !== m);
    m.delSib(this);
    if (this.parentalUnions.length === 0) {
      this.parents = null;
    } else {
      this.parents = this.parentalUnions[this.parentalUnions.length - 1];
    }
  }

  delPerson() {
    this.parents = null;
    for (const pu of [...this.parentalUnions]) {
      pu.delSib(this);
    }
    this.parentalUnions = [];
    for (const m of [...this.marriages]) {
      this.delMarriage(m);
    }
    this.marriages = [];
  }

  hasBegun() {
    if (this.yob !== 'NA' && state.refYear !== '') {
      const yob = parseInt(this.yob, 10);
      const ref = parseInt(state.refYear, 10);
      if (!isNaN(yob) && !isNaN(ref)) return yob <= ref;
    }
    return true;
  }

  hasEnded() {
    if (this.yod !== 'NA' && state.refYear !== '') {
      const yod = parseInt(this.yod, 10);
      const ref = parseInt(state.refYear, 10);
      if (!isNaN(yod) && !isNaN(ref)) return yod <= ref;
    }
    return false;
  }

  getLabel() {
    const doLabel = state.doLabel;
    if (doLabel === NOLABEL) return null;
    const parts = this.name.trim().split(/\s+/);
    if (parts.length === 0 || (parts.length === 1 && parts[0] === '')) return 'None';

    if (doLabel === FIRST) {
      if (parts.length <= 1) return parts[0] || 'None';
      return parts[0] + ' ' + parts.slice(1).map(p => p[0]).join('');
    }
    if (doLabel === LAST) {
      if (parts.length <= 1) return parts[0] || 'None';
      return parts.slice(0, -1).map(p => p[0]).join('') + ' ' + parts[parts.length - 1];
    }
    if (doLabel === INITIALS) {
      return parts.map(p => p[0]).join('');
    }
    if (doLabel === WHOLE) {
      return this.name || 'None';
    }
    return null;
  }
}

// Marriage
export class Marriage {
  constructor(x, y) {
    this.location = x !== null ? { x, y } : null;
    this.mid = state.knotIndex + 2;
    this.id = state.knotIndex + 2;
    this.type = 'Marriage';
    this.begin = 'NA';
    this.beginq = '9';
    this.end = 'NA';
    this.endq = '9';
    this.comment = 'No Comment';
    this.reason = 'NA';
    this.spouses = [];  // Person[]
    this.sibset = [];   // Person[]
    this.drawn = false;
  }

  get size() { return state.symbolSize; }

  bounds() {
    return { x: this.location.x, y: this.location.y, width: this.size, height: this.size };
  }

  isSpouse(p) { return this.spouses.includes(p); }
  isSib(p) { return this.sibset.includes(p); }

  addSpouse(p) {
    if (!this.spouses.includes(p)) this.spouses.push(p);
    if (!p.marriages.includes(this)) p.marriages.push(this);
  }

  delSpouse(p) {
    this.spouses = this.spouses.filter(x => x !== p);
    p.marriages = p.marriages.filter(x => x !== this);
  }

  addSib(p) {
    if (!this.sibset.includes(p)) this.sibset.push(p);
    if (p.parents !== this) p.setParents(this);
  }

  delSib(p) {
    if (this.sibset.includes(p)) {
      this.sibset = this.sibset.filter(x => x !== p);
      p.parentalUnions = p.parentalUnions.filter(x => x !== this);
      if (p.parentalUnions.length === 0) {
        p.parents = null;
      } else {
        p.parents = p.parentalUnions[p.parentalUnions.length - 1];
      }
    }
  }

  delMarriage() {
    for (const p of [...this.spouses]) {
      this.delSpouse(p);
    }
    for (const p of [...this.sibset]) {
      this.delSib(p);
    }
  }

  deltaMove(dx, dy) {
    this.location.x -= dx;
    this.location.y -= dy;
    for (const p of this.spouses) {
      p.location.x -= dx;
      p.location.y -= dy;
    }
    for (const p of this.sibset) {
      p.location.x -= dx;
      p.location.y -= dy;
    }
  }

  lineageDeltaMove(dx, dy) {
    this.location.x -= dx;
    this.location.y -= dy;
    for (const p of this.spouses) {
      p.location.x -= dx;
      p.location.y -= dy;
    }
    for (const p of this.sibset) {
      if (p.marriages.length === 0) {
        p.location.x -= dx;
        p.location.y -= dy;
      } else {
        for (const m of p.marriages) {
          m.lineageDeltaMove(dx, dy);
        }
      }
    }
  }

  hasBegun() {
    if (this.begin !== 'NA' && state.refYear !== '') {
      const begin = parseInt(this.begin, 10);
      const ref = parseInt(state.refYear, 10);
      if (!isNaN(begin) && !isNaN(ref)) return begin <= ref;
    }
    return true;
  }

  hasEnded() {
    if (this.end !== 'NA' && state.refYear !== '') {
      const end = parseInt(this.end, 10);
      const ref = parseInt(state.refYear, 10);
      if (!isNaN(end) && !isNaN(ref)) return end <= ref;
    }
    return false;
  }
}

// Utility functions
export function findPerson(x, y) {
  for (let i = state.folkIndex; i >= 0; i--) {
    const p = state.folks[i];
    if (!p) continue;
    const b = p.bounds();
    if (x >= b.x && x <= b.x + b.width && y >= b.y && y <= b.y + b.height)
      return i;
  }
  return -1;
}

export function findMarriage(x, y) {
  for (let i = state.knotIndex; i >= 0; i--) {
    const m = state.knots[i];
    if (!m) continue;
    const b = m.bounds();
    if (x >= b.x && x <= b.x + b.width && y >= b.y && y <= b.y + b.height)
      return i;
  }
  return -1;
}

export function findFreePerson() {
  for (let i = state.folkIndex; i >= 0; i--) {
    if (!state.folks[i]) return i;
  }
  return ++state.folkIndex;
}

export function findFreeMarriage() {
  for (let i = state.knotIndex; i >= 0; i--) {
    if (!state.knots[i]) return i;
  }
  return ++state.knotIndex;
}

export function resetBoundingBox() {
  let maxx = -20000, maxy = -20000, minx = 20000, miny = 20000;
  for (let i = 0; i <= state.folkIndex; i++) {
    const p = state.folks[i];
    if (p) {
      if (p.location.x > maxx) maxx = p.location.x;
      if (p.location.x < minx) minx = p.location.x;
      if (p.location.y > maxy) maxy = p.location.y;
      if (p.location.y < miny) miny = p.location.y;
    }
  }
  return { minx, miny, maxx, maxy };
}
