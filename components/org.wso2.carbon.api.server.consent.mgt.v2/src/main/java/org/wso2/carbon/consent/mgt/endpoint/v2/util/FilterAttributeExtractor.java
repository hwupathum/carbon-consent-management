/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.consent.mgt.endpoint.v2.util;

import org.wso2.carbon.identity.core.model.ExpressionNode;
import org.wso2.carbon.identity.core.model.Node;
import org.wso2.carbon.identity.core.model.OperationNode;

import java.util.UUID;

/**
 * Extracts filter attributes from filter expressions.
 * Converts parsed filter expressions into individual attribute values.
 */
public class FilterAttributeExtractor {

    /**
     * Extracts filter attributes from a filter tree Node.
     * Supports extracting attributes for consents: subjectId, serviceId, purposeId, state
     */
    public FilterAttributes extract(Node rootNode) {

        FilterAttributes attrs = new FilterAttributes();
        extractAttributes(rootNode, attrs);
        return attrs;
    }

    private void extractAttributes(Node node, FilterAttributes attrs) {

        if (node == null) {
            return;
        }

        if (node instanceof ExpressionNode) {
            extractFromExpression((ExpressionNode) node, attrs);
        } else if (node instanceof OperationNode) {
            // For logical operations, traverse both left and right nodes
            extractAttributes(node.getLeftNode(), attrs);
            extractAttributes(node.getRightNode(), attrs);
        }
    }

    private void extractFromExpression(ExpressionNode exprNode, FilterAttributes attrs) {

        String attribute = exprNode.getAttributeValue();
        if (attribute == null) {
            return;
        }

        attribute = attribute.toLowerCase();
        String value = exprNode.getValue();

        // Extract the value regardless of operator
        // Backend DAOs will handle the filtering with LIKE queries
        switch (attribute) {
            case "subjectid":
                attrs.setSubjectId(value);
                break;
            case "serviceid":
                attrs.setServiceId(value);
                break;
            case "state":
                attrs.setState(value);
                break;
            case "purposeid":
                try {
                    attrs.setPurposeId(UUID.fromString(value));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
                break;
            // For purposes endpoint
            case "type":
                attrs.setType(value);
                break;
            case "name":
                attrs.setName(value);
                break;
            default:
                // Unknown attribute, skip
        }
    }

    /**
     * Container for extracted filter attributes.
     */
    public static class FilterAttributes {

        private String subjectId;
        private String serviceId;
        private UUID purposeId;
        private String state;
        private String type;
        private String name;

        public String getSubjectId() {

            return subjectId;
        }

        public void setSubjectId(String subjectId) {

            this.subjectId = subjectId;
        }

        public String getServiceId() {

            return serviceId;
        }

        public void setServiceId(String serviceId) {

            this.serviceId = serviceId;
        }

        public UUID getPurposeId() {

            return purposeId;
        }

        public void setPurposeId(UUID purposeId) {

            this.purposeId = purposeId;
        }

        public String getState() {

            return state;
        }

        public void setState(String state) {

            this.state = state;
        }

        public String getType() {

            return type;
        }

        public void setType(String type) {

            this.type = type;
        }

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }
    }
}
