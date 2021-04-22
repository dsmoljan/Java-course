package hr.fer.oprpp1.hw04.db;

import java.util.List;

public class QueryFilter implements IFilter{
	
	private List<ConditionalExpression> list;
	
	public QueryFilter(List<ConditionalExpression> list) {
		this.list = list;
	}

	@Override
	public boolean accepts(StudentRecord record) {
		boolean accept = true;

		for (ConditionalExpression e : this.list) {
			
			
			String value;

			
			if (e.getFieldGetter().equals(FieldValueGetters.FIRST_NAME)){
				value = record.getFirstName();
			} else if (e.getFieldGetter().equals(FieldValueGetters.LAST_NAME)) {
				value = record.getLastName();
			} else {
				value = record.getJMBAG();
			}
			
			if (e.getComparisonOperator().satisfied(value, e.getStringLiteral())) {
				accept = true;
			} else{
				return false;
			};
		}
		return accept;
	}

}
