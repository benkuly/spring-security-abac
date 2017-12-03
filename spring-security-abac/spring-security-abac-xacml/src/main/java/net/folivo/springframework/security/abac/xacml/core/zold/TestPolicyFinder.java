package net.folivo.springframework.security.abac.xacml.core.zold;
/*-
import com.att.research.xacml.api.IdReferenceMatch;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdVersion;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.Policy;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.att.research.xacmlatt.pdp.policy.PolicyFinder;
import com.att.research.xacmlatt.pdp.policy.PolicyFinderResult;
import com.att.research.xacmlatt.pdp.policy.PolicySet;
import com.att.research.xacmlatt.pdp.std.StdPolicyFinderResult;

public class TestPolicyFinder implements PolicyFinder {

	@Override
	public PolicyFinderResult<PolicyDef> getRootPolicyDef(EvaluationContext evaluationContext) {
		PolicySet policy = new PolicySet();
		policy.setIdentifier(new IdentifierImpl("core.springapp"));
		policy.setVersion(new StdVersion(new int[] { 1, 0, 0 }));
		return new StdPolicyFinderResult<>(StdStatus.STATUS_OK, policy);
	}

	@Override
	public PolicyFinderResult<Policy> getPolicy(IdReferenceMatch idReferenceMatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PolicyFinderResult<PolicySet> getPolicySet(IdReferenceMatch idReferenceMatch) {
		// TODO Auto-generated method stub
		return null;
	}

}
*/