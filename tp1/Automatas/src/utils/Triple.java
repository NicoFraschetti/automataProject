package utils;

/**
 *	Implements an immutable 3-uple. 
*/
public class Triple<A,B,C> {

	// Private attributes
	
	private A _first;
	
	private B _second;
	
	private C _third;
	
	// Construction
	
	public Triple(A fst, B snd, C trd) {
		_first = fst;
		_second = snd;
		_third = trd;
	}
	
	// Getters
	
	public A first() { 
		return _first;
	}
	
	public B second() { 
		return _second; 
	}
	
	public C third() { 
		return _third;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_first == null) ? 0 : _first.hashCode());
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());
		result = prime * result + ((_third == null) ? 0 : _third.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triple other = (Triple) obj;
		if (_first == null) {
			if (other._first != null)
				return false;
		} else if (!_first.equals(other._first))
			return false;
		if (_second == null) {
			if (other._second != null)
				return false;
		} else if (!_second.equals(other._second))
			return false;
		if (_third == null) {
			if (other._third != null)
				return false;
		} else if (!_third.equals(other._third))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "["+first()+","+second()+","+third()+"]";
	}
	
}
