package automata;

public class State {

	private String name;
	
	//true when the state is marked to be a final state
	private boolean isFinal;
	
	//true when the state is marked to be a initial state
	private boolean isInitial;
	
	public State(String name, boolean isInitial, boolean isFinal) {
		this.name = name;
		this.isInitial = isInitial;
		this.isFinal = isFinal;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public boolean isFinal(){
		return isFinal;
	}

	public boolean isInitial(){
		return isInitial;
	}
	
	public State complement() {
		if (isFinal)
			return new State(name, isInitial, false);
		else
			return new State(name, isInitial, true);
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		State other = (State) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}

	

}
