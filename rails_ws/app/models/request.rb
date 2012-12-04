class Request < ActiveRecord::Base
  attr_accessible :error, :intent_program, :num_errors, :num_phones, :output, :requester_device_ident, :status
end
