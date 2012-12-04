class CreateRequests < ActiveRecord::Migration
  def change
    create_table :requests do |t|
      t.string :requester_device_ident
      t.string :intent_program
      t.string :output
      t.string :error
      t.string :status
      t.integer :num_phones
      t.integer :num_errors

      t.timestamps
    end
  end
end
